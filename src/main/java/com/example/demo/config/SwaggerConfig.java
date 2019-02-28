package com.example.demo.config;

import io.github.swagger2markup.GroupBy;
import io.github.swagger2markup.Language;
import io.github.swagger2markup.Swagger2MarkupConfig;
import io.github.swagger2markup.Swagger2MarkupConverter;
import io.github.swagger2markup.builder.Swagger2MarkupConfigBuilder;
import io.github.swagger2markup.markup.builder.MarkupLanguage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.async.DeferredResult;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Predicates.or;
import static springfox.documentation.builders.PathSelectors.regex;


@EnableSwagger2
@Configuration
public class SwaggerConfig {
    private static final String GateWay_API = "gateway";
    private static final String Other_API = "other";

    final String userAgentJson = "{\"id\":int,\"uid\":string,\"openid\":string,\"token\":string,\"lastUid\":string,\"platform\":int}";

    @Bean
    public Docket gatewayAPI() {
        List<Parameter> pars = addToken();
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName(GateWay_API)
                .genericModelSubstitutes(DeferredResult.class)
                .useDefaultResponseMessages(false)
                .forCodeGeneration(true)
                .pathMapping("/")
                .select()
                .paths(or(
                        regex("/login/.*"),
                        regex("/gw/.*")
                ))
                .build()
                .globalOperationParameters(pars)
                .apiInfo(setApiInfo("提供网关功能与数据接口"));
    }

    private List<Parameter> addToken() {
        ParameterBuilder tokenPar = new ParameterBuilder();
        ParameterBuilder userAgentPar = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<Parameter>();
        userAgentPar.name("userAgent").description(userAgentJson).modelRef(new ModelRef("string")).parameterType("header").required(false).defaultValue("").build();
        tokenPar.name("accesstoken").description("accesstoken").modelRef(new ModelRef("string")).parameterType("header").required(false).defaultValue("").build();
        pars.add(tokenPar.build());
        pars.add(userAgentPar.build());
        return pars;
    }

    @Bean
    public Docket otherAPI() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName(Other_API)
                .genericModelSubstitutes(DeferredResult.class)
                .useDefaultResponseMessages(false)
                .forCodeGeneration(true)
                .pathMapping("/")
                .select()
                .paths(or(
                        regex("/admin/.*"),
                        regex("/job/.*"),
                        regex("/common/.*"),
                        regex("/index/.*"),
                        regex("/upload/.*"),
                        regex("/weixin/.*"),
                        regex("/wx/.*"),
                        regex("/login/.*"),
                        regex("/test/.*")
                ))
                .build()
                .apiInfo(setApiInfo("提供功能与数据接口"));
    }

    private ApiInfo setApiInfo(String description) {
        ApiInfo apiInfo = new ApiInfo(
                "平台API",
                description,
                "1.0",
                "No terms of service",
                "1193169983@qq.com",
                "The Apache License, Version 2.0",
                "http://www.apache.org/licenses/LICENSE-2.0.html"
        );
        return apiInfo;
    }


    /**
     * 生成html文章专用
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        String groupName="other";
        // String groupName="gateway";

        URL remoteSwaggerFile = new URL("http://127.0.0.1:8080//v2/api-other?group="+groupName);
        Path outputFile = Paths.get("other-co/other-co-wlyy/build/"+groupName);

        Swagger2MarkupConfig config = new Swagger2MarkupConfigBuilder()
                .withMarkupLanguage(MarkupLanguage.ASCIIDOC)
                .withOutputLanguage(Language.ZH)
                .withPathsGroupedBy(GroupBy.TAGS)
                .withGeneratedExamples()
                .withoutInlineSchema()
                .withBasePathPrefix()
                .build();

        Swagger2MarkupConverter converter = Swagger2MarkupConverter.from(remoteSwaggerFile)
                .withConfig(config)
                .build();

        converter.toFile(outputFile);
    }
}