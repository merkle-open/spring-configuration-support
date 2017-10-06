# Web Support for config administration

## Setup

__Make sure that your Security-Configuration enforces access restrictions to this servlet!__

### Spring Boot with ServletRegistrationBean

```java
@Bean(name = "configServlet")
public ServletRegistrationBean configServlet() {
	DispatcherServlet dispatcherServlet = new DispatcherServlet();
	AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
	applicationContext.register(ConfigServletConfig.class);
	dispatcherServlet.setApplicationContext(applicationContext);
	ServletRegistrationBean registrationBean = new ServletRegistrationBean(dispatcherServlet, "/config/*");
	registrationBean.setLoadOnStartup(1);
	return registrationBean;
}
``` 

### Plain Spring with WebApplicationInitializer

```java
import com.namics.commons.config.web.ConfigServletConfig;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class ConfigServletInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
	@Override
	protected String getServletName() {
		return "config";
	}

	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class<?>[] {};
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class<?>[] { ConfigServletConfig.class };
	}

	@Override
	protected String[] getServletMappings() {
		return new String[] { "/admin/config/*" };
	}

}
```

### Plain Spring with code based Java servlet configuration

```java
public static void configServlet(ServletContext servletContext, boolean asyncSupported) {
	String servletName = "config";
	AnnotationConfigWebApplicationContext servletAppContext = new AnnotationConfigWebApplicationContext();
	servletAppContext.register(ConfigServletConfig.class);
	DispatcherServlet dispatcherServlet = new DispatcherServlet(servletAppContext);
	ServletRegistration.Dynamic registration = servletContext.addServlet(servletName, dispatcherServlet);
	registration.setLoadOnStartup(5);
	registration.addMapping("/admin/config/*");
	registration.setAsyncSupported(asyncSupported);
}
```

### web.xml

TBD.


## Security and CORS

The js app will automatically setup CORS-Header for ajax requests, if required parameters are provided on integration.

If you use iframe integration, provide the following params with your src url.

```html
<iframe src="/admin/config/properties.html#csrf=${_csrf.token}&csrf_header=${_csrf.headerName}" width="100%"></iframe>
```

Param        | Value
-------------|--------------------------------------------------
csrf         | actual token to be send with every request 
csrf_header  | HTTP-Header name to send token with every request
 
 
