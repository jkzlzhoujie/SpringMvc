//本地
var domainName = "http://localhost:8080";//上传域名

function login() {
    var username = document.getElementById("username").value;
    var pass = document.getElementById("password").value;
    if (username == "") {
        alert("请输入用户名");
    } else if (pass == "") {
        alert("请输入密码");
    } else if (username == "admin" && pass == "123456") {
        alert(pass);
        window.location.href = domainName + "/cmUser/findByName?name=" + username;
    } else {
        $.ajax({
            url: domainName + "/login",
            type: 'post',
            data: {
                loginName: username,
                password: pass
            },
            dataType: "json",
            success: function (data) {
                if (data == null) {
                    alert("请输入正确的用户名和密码！")
                } else {
                   // window.location.href = domainName + "hello";
                }
            }
        });
    }
};