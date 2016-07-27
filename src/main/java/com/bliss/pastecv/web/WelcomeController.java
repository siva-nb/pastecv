package com.bliss.pastecv.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import org.springframework.web.bind.annotation.RequestParam;
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
	
	@RequestMapping(value="/home", method = RequestMethod.GET)
	public String home(Map<String, Object> model){
		logger.debug("home() is executed");
		
		return "home1";
	}
	
	private String streamToString(InputStream is) throws IOException{
		StringBuilder line = new StringBuilder();
		String l = null;
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(is));
		    while ((l = in.readLine()) != null) {
		        line.append(l);
		    }
		    
		    return line.toString();
	}
	
	@RequestMapping(value="/submit", method = RequestMethod.POST)
	public String submit(@RequestParam("code") String code){
		logger.debug("submit() is executed");
		logger.debug(code);
		
		String path = "Users/sivanatarajanbalasubramania/Documents/eclipse_workspace/pastecv/src/main/webapp/resources/";
		File file = new File("main.java");
		
		FileWriter fwWriter = null;
		try {
			fwWriter = new FileWriter(file);
			fwWriter.write(code);
			fwWriter.flush();
			fwWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String command = "javac main.java";
		
		Process pro = null;
		try {
			 pro = Runtime.getRuntime().exec(command);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			logger.debug(streamToString(pro.getInputStream()));
			logger.debug(streamToString(pro.getErrorStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}		
		
		return "index";
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