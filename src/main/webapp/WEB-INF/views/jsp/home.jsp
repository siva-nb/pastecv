<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>
<title>Gradle + Spring MVC</title>

<spring:url value="/resources/core/css/hello.css" var="coreCss" />
<spring:url value="/resources/core/css/bootstrap.min.css" var="bootstrapCss" />
<link href="${bootstrapCss}" rel="stylesheet" />
<link href="${coreCss}" rel="stylesheet" />
</head>

	<nav class="navbar navbar-inverse navbar-fixed-top">
	<div class="container">
		<div class="navbar-header">
			<a class="navbar-brand" href="#">Project Name</a>
		</div>
	</div>
</nav>
<div class="jumbotron">
	<div class="container">
		<p>
			<a class="btn btn-primary btn-lg" href="#" role="button">Learn more</a>
		</p>
	</div>
</div>


<div class="container">
	<form name="submit-form" action="submit" method="POST">
	<div class="row">
		<div class="col-md-8">
			<h2>Heading</h2>
			<div class="form-group">
  				<label for="comment">Comment:</label>
  				<textarea class="form-control" rows="5" name="code" id="code"></textarea>
			</div>
			<p>
				<input type="submit" class="btn btn-default" role="button" >Submit</input>
			</p>
		</div>
		</div>
	</form>
</div>


</html>