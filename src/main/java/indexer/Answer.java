package indexer;

/**
 * Answer object that is attached to a question to calculate score
 * @author Narasimman
 *
 */
public class Answer {
  private final int id;
  private final int score;
  private final String body;

  public Answer(int id, int score, String body) {
    if(id == 0) {
      throw new IllegalArgumentException("Invalid Id for answer");
    }

    this.id = id;
    this.score = score;
    this.body = body;
  }

  public int getId() {
    return id;
  }

  public int getScore() {
    return score;
  }
  
  public String getBody() {
    return body;
  }

  @Override
  public String toString() {
    return "Answer [id=" + id + ", score=" + score + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + id;
    result = prime * result + score;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof Answer)) {
      return false;
    }
    Answer other = (Answer) obj;
    if (id != other.id) {
      return false;
    }
    if (score != other.score) {
      return false;
    }
    return true;
  }
}
