package com.bliss.pastecv.web;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.bliss.pastecv.messaging.RabbitMQConfig;
import com.bliss.pastecv.service.HelloWorldService;
import com.bliss.pastecv.service.Receiver;

@Controller
public class WelcomeController {
	
	final static String queueName = "spring-boot";
	private final Logger logger = LoggerFactory.getLogger(WelcomeController.class);
	private final HelloWorldService helloWorldService;

	@Autowired
	public WelcomeController(HelloWorldService helloWorldService) {
		this.helloWorldService = helloWorldService;
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(Map<String, Object> model) {

		logger.debug("index() is executed!");

		model.put("title", helloWorldService.getTitle(""));
		model.put("msg", helloWorldService.getDesc());
		
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(RabbitMQConfig.class);
        ctx.refresh();
	   System.out.println("---Message is being sent---");
	   RabbitTemplate rabbitTemplate = (RabbitTemplate)ctx.getBean("rabbitTemplate");
	   Receiver receiver = (Receiver)ctx.getBean("receiver");
	   rabbitTemplate.convertAndSend(queueName, "Hello World!");
	   try {
		   receiver.getLatch().await(10000, TimeUnit.MILLISECONDS);
	   } catch (InterruptedException e) {
		   e.printStackTrace();
	   }
	   ctx.close();
	   
		return "index";
	}

	@RequestMapping(value = "/hello/{name:.+}", method = RequestMethod.GET)
	public ModelAndView hello(@PathVariable("name") String name) {

		logger.debug("hello() is executed - $name {}", name);

		ModelAndView model = new ModelAndView();
		model.setViewName("index");
		
		model.addObject("title", helloWorldService.getTitle(name));
		model.addObject("msg", helloWorldService.getDesc());
		
		return model;

	}

}