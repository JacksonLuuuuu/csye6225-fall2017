/**
 * Amitha_Murali, 001643826, murali.a@husky.neu.edu
 * Jyoti Sharma, 001643410, sharma.j@husky.neu.edu
 * Surabhi Patil, 001251860, patil.sur@husky.neu.edu
 **/

package com.csye6225.demo.controllers;


import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import com.csye6225.demo.entities.User;
import com.csye6225.demo.dao.UserRepository;
import com.csye6225.demo.helpers.Helper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.csye6225.demo.dao.DynamodbItem;

import java.util.List;
import java.util.UUID;

@Controller    // This means that this class is a Controller
@RequestMapping(path="/user") // This means URL's start with /user (after Application path)

public class RegisterUserController {

    @Autowired // This means to get the bean called userRepository which is auto-generated by Spring, we will use it to handle the data
    private UserRepository userRepository;


    @Autowired
    private Helper helper;


    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    //@GetMapping(path="/register") // Map ONLY GET Requests
    @RequestMapping(value="/register",method=RequestMethod.POST,produces="application/json")
    public @ResponseBody String addNewUser (@RequestBody User user, HttpServletRequest request, HttpServletResponse response){
        //@RequestParam String email, @RequestParam String password) {
        // @ResponseBody means the returned String is the response, not a view name
        // @RequestParam means it is a parameter from the GET or POST request
        //String email = request.getParameter("email");


        if(helper.validateUserEmail(user.getEmail()) == null) {
            User newUser = new User();
            newUser.setName(user.getName());
            newUser.setEmail(user.getEmail());   //
            newUser.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            userRepository.save(newUser);

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("message", "User has been created successfully.");
            //jsonObject.addProperty("name",newUser.getName());
            jsonObject.addProperty("email",newUser.getEmail());
            jsonObject.addProperty("password",newUser.getPassword());
            return jsonObject.toString();
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("message", "User already exists.");
        return jsonObject.toString();
    }


    @GetMapping(path="/all")
    public @ResponseBody Iterable<User> getAllUsers() {
        // This returns a JSON or XML with the users
        return userRepository.findAll();
    }

    @Autowired
    private AmazonSNSClient amazonSNSClient;

    @Autowired
    private AmazonDynamoDB amazonDynamoDBClient;


    @RequestMapping(value="/resetpassword",method=RequestMethod.POST,produces="application/json")
    public @ResponseBody String resetPassword (@RequestBody User user, HttpServletRequest request, HttpServletResponse response) {

        if(helper.validateUserEmail(user.getEmail()) != null) {

            DynamodbItem item = new DynamodbItem();
            DynamoDBMapper mapper = new DynamoDBMapper(amazonDynamoDBClient);

           // item.setKey(user.getEmail());
          //  DynamoDBQueryExpression<DynamodbItem> queryExpression = new DynamoDBQueryExpression<DynamodbItem>()
           //         .withHashKeyValues(item);

           // List<DynamodbItem> itemList = mapper.query(DynamodbItem.class, queryExpression);

         //   if(itemList.size() == 0) {
                //Add the token and email id to dynamo DB
                item.setKey(user.getEmail());
                UUID token = UUID.randomUUID();
                item.setValue(token);
                mapper.save(item);


                //Publish a message to the SNS topic
                String msg = "My text published to SNS topic with email endpoint";
                PublishRequest publishRequest = new PublishRequest("arn:aws:sns:us-east-1:306856603029:demo_csye6225", msg);
                PublishResult publishResult = amazonSNSClient.publish(publishRequest);


                String emailMsg = "http://localhost:8000/reset?email=" + user.getEmail() + "&token=" + token;
                PublishRequest emailPublishRequest = new PublishRequest("arn:aws:sns:us-east-1:306856603029:NotifyMe", emailMsg);
                PublishResult emailPublishResult = amazonSNSClient.publish(emailPublishRequest);
          //  }

        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("message", "Please check your email for a reset link.");
        return jsonObject.toString();
    }

    }