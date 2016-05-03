<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css">
<title>Search Overflow</title>
</head>
<body>
	<div class="container text-center">
		<h1>Search Overflow</h1>
		<p>A Stack Overflow Search Engine</p>

		<form action="search" name="searchOverflow" class="form-horizontal">
			<div class="row">
			  <input type="text" name="q" placeholder="Search here..." class="form-control" />
			</div> 
		  
		  <div style="margin:20px;">
		    <button type="submit" value="search"	class="btn btn-default"> Search </button>
		  </div>
		</form>

		<br>

		<div id="result" style="background-color: #EEE;width: 600px;height: 200px; margin:auto; border-radius:10px;">
		<div id="loading" style="display:none"><img src="loading.gif" /></div>
		
		</div>
		<div style="position:absolute;bottom:0px;">
		  <p>Powered by Narasimman & Manasa</p>
		</div>
	</div>
	<script type="text/javascript"
		src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.0.0-beta1/jquery.min.js"></script>

	<script type="text/javascript">
	// magic.js
	$(document).ready(function() {
		  var loading = $("#loading");
      $(document).ajaxStart(function () {
            loading.show();
      });

      $(document).ajaxStop(function () {
            //loading.hide();
      });
		
		
	    $('form').submit(function(event) {
	        var formData = {
	            'q'  : $('input[name=q]').val(),	            
	        };

	        $.ajax({
	            type        : 'GET',
	            url         : 'search',
	            data        : formData
	        }).done(function(data) {
	                $('#result').html(data); 
          });
	        event.preventDefault();
	    });
	});
  
  </script>
</body>
</html>