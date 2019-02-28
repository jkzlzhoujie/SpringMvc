package com.example.demo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.hamcrest.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Created by janseny on 2018/11/30.
 */

@WebAppConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest()
public class TestWebController {
    
    @Autowired
    private WebApplicationContext webAppContext; //注入WebApplicationContext

    private MockMvc mockMvc;

    @Before
    public void setupMockMvc(){//设置 MockMvc
        mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
    }

    @Test
    public void testWebLoginPage() throws Exception{
        System.out.println("web test");
        mockMvc.perform(MockMvcRequestBuilders.get("/login/login"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("login/login"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("user"))
                .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.is(Matchers.empty())));

    }

    @Test
    public void testWebPage() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.post("/login/loginPost").contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("title", "测试")
                .param("author", "测试")
                .param("note","备注"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
        .andExpect(MockMvcResultMatchers.header().string("Location","login/index"));
    }



}
