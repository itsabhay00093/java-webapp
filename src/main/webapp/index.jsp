<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html>
<head>
  <meta charset="UTF-8"/>
  <title>Demo Webapp</title>
</head>
<body>
  <h1>Demo Webapp</h1>
  <form method="get" action="greet">
    <label>Enter name: <input type="text" name="name" /></label>
    <button type="submit">Greet</button>
  </form>
  <p>Or try: <a href="greet?name=Student">/greet?name=Student</a></p>
</body>
</html>

