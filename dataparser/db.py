import sqlite3
import os
import xml.etree.cElementTree as etree
import logging

schema = {
  'Posts': {
    'Id': 'INTEGER',
    'PostTypeId': 'INTEGER',  # 1: Question, 2: Answer
    'ParentID': 'INTEGER',  # (only present if PostTypeId is 2)
    'AcceptedAnswerId': 'INTEGER',  # (only present if PostTypeId is 1)
    'CreationDate': 'DATETIME',
    'Score': 'INTEGER',
    'ViewCount': 'INTEGER',
    'Body': 'TEXT',
    'OwnerUserId': 'INTEGER',  # (present only if user has not been deleted)
    'OwnerDisplayName': 'TEXT',
    'LastEditorUserId': 'INTEGER',
    'LastEditorDisplayName': 'TEXT',  # ="Rich B"
    'LastEditDate': 'DATETIME',  #="2009-03-05T22:28:34.823"
    'LastActivityDate': 'DATETIME',  #="2009-03-11T12:51:01.480"
    'CommunityOwnedDate': 'DATETIME',  #(present only if post is community wikied)
    'Title': 'TEXT',
    'Tags': 'TEXT',
    'AnswerCount': 'INTEGER',
    'CommentCount': 'INTEGER',
    'FavoriteCount': 'INTEGER',
    'ClosedDate': 'DATETIME'
  }
}


def dump_files(file_names, anathomy,
  dump_path='/home/ns3184/searchoverflow',
  dump_database_name='so-dump.db',
  create_query='CREATE TABLE IF NOT EXISTS {table} ({fields})',
  insert_query='INSERT INTO {table} ({columns}) VALUES ({values})', log_filename='so-parser.log'):
  logging.basicConfig(filename=os.path.join(dump_path, log_filename), level=logging.INFO)
  db = sqlite3.connect(os.path.join(dump_path, dump_database_name))
  
  for file in file_names:
    print "Opening {0}.xml".format(file)
    with open(os.path.join(dump_path, file + '.xml')) as xml_file:
      tree = etree.iterparse(xml_file)
      table_name = file

      sql_create = create_query.format(
        table=table_name,
        fields=", ".join(['{0} {1}'.format(name, type) for name, type in schema[table_name].items()]))
      print('Creating table {0}'.format(table_name))

      try:
        logging.info(sql_create)
        db.execute(sql_create)
      except Exception, e:
        logging.warning(e)
	    
      for events, row in tree:
        try:
          if row.attrib.values():
            #print(row.attrib.keys())
            query = insert_query.format(
              table=table_name,
              columns=', '.join(row.attrib.keys()),
              values=('?, ' * len(row.attrib.keys()))[:-2])
            db.execute(query, row.attrib.values())
            print ".",
        except Exception, e:
          logging.warning(e)
          print "x",
        finally:
          row.clear()
      print "\n"
      db.commit()
      del (tree)

if __name__ == '__main__':
    dump_files(["Posts"], schema)
