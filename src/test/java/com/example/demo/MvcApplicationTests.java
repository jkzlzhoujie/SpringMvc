package com.example.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

//@SpringBootTest 标注一个测试类，如果该注解没有指定加载的启动配置类，
//那么会自动搜索标注有@SpringBootApplication注解的类。如果需要手工指定，
//那么通过注解的classes属性即可。找到启动类后注入服务，随后可以进行测试。
@RunWith(SpringRunner.class)
@SpringBootTest()
public class MvcApplicationTests {

	@Test
	public void contextLoads() {
		System.out.println("测试 加载。。。");
	}

}
