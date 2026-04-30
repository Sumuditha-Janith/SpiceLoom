package lk.ijse.gdse71.spiceloom.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map requests starting with /FrontEnd/ to the FrontEnd folder inside resources
        registry.addResourceHandler("/FrontEnd/**")
                .addResourceLocations("classpath:/FrontEnd/");
        // Also map root-level files like index.html
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/FrontEnd/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/index.html");
    }
}