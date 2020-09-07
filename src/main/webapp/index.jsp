<%@ page language="java" contentType="text/html; charset=UTF-8" %>

<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>背单词</title>
    <script type="text/javascript" src="http://libs.baidu.com/jquery/2.0.0/jquery.min.js"></script>
    
	<script type="text/javascript">
	//删除
		function del(key) {
			console.log(key);
			 localStorage.removeItem(key);
			 $('#'+key).hide();
		}
		function tog(key) {
			 $('#'+key).toggle();
		}
		$(document).ready(function(){
					
			var len = localStorage.length;
			if(len > 20){
				len = 20;
			}
			for (var i = 0; i < len; i++) {
				var key = localStorage.key(i); //获取本地存储的Key console.log(key);... 
				var $tr = $("<tr id='"+key+"'></tr>");

				$tr.append("<td><button onclick=del('"+key+"')>删</button></td>");
				$tr.append("<td>"+ key +"</td>");

				$tr.append("<td><button onclick=tog('"+key+"_value')>切</button></td>");
				$tr.append("<td id='"+key+"_value'>"+ localStorage.getItem(key) +"</td>");
				$("tbody").append($tr);
			}
	
		});
	</script>
	
</head>
<body>
    <header>
    </header>
    <div id="container">
        <nav>
            <a href="text.jsp">英文文本</a>
            <a href="upload.jsp">上传文件</a>
            <hr/>
        </nav>
        <section>
          <table>
		    <thead>
		        <tr>
		            <th>单词</th>
		            <th>含义</th>
		        </tr>
		    </thead>
		
		    <tbody>
		
		    </tbody>
		</table>
        </section>
        <aside>
            <h3></h3>
            <p></p>
        </aside>
        <footer>
            <h2></h2>
        </footer>
    </div>
</body>
</html>