package server.config;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HTTPSRedirection {
	@Value("${server.port.http}")
	private int serverPortHttp;

	@Value("${server.port}")
	private int serverPortHttps;
	
	@Bean
	public EmbeddedServletContainerFactory servletContainer() {
		TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory() {
			@Override
			protected void postProcessContext(Context context) {
				SecurityConstraint securityConstraint = new SecurityConstraint();
				securityConstraint.setUserConstraint("CONFIDENTIAL");
				SecurityCollection collection = new SecurityCollection();
				collection.addPattern("/add/*");
				collection.addPattern("/get/*");
				collection.addPattern("/raspberry");
				collection.addPattern("/speech");
				collection.addPattern("/report/*");
				collection.addPattern("/accompanist");
				collection.addPattern("/observation/*");
				collection.addPattern("/account");
				collection.addPattern("/login");
				collection.addPattern("/forgot");
				securityConstraint.addCollection(collection);
				context.addConstraint(securityConstraint);
			}
		};

		tomcat.addAdditionalTomcatConnectors(initiateHttpConnector());
		return tomcat;
	}

	private Connector initiateHttpConnector() {
		Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
		connector.setScheme("http");
		connector.setPort(serverPortHttp);
		connector.setSecure(false);
		connector.setRedirectPort(serverPortHttps);
		return connector;
	}
}
