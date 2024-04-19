package com.app.mahesh;

import java.util.List;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.app.mahesh.model.Student;

public class StudentService {
	private DynamoDBMapper dynamoDBMapper;
	private static String jsonBody = null;

	public APIGatewayProxyResponseEvent saveStudent(APIGatewayProxyRequestEvent apiGatewayRequest, Context context) {
		initDynamoDB();
		Student student = Utility.convertStringToObj(apiGatewayRequest.getBody(), context);
		dynamoDBMapper.save(student);
		jsonBody = Utility.convertObjToString(student, context);
		context.getLogger().log("data saved successfully to dynamodb:::" + jsonBody);
		return createAPIResponse("data saved successfully to dynamodb:::" + jsonBody, 201, Utility.createHeaders());
	}

	public APIGatewayProxyResponseEvent getStudentById(APIGatewayProxyRequestEvent apiGatewayRequest, Context context) {
		initDynamoDB();
		String stdId = apiGatewayRequest.getPathParameters().get("stdId");
		Student student = dynamoDBMapper.load(Student.class, stdId);
		if (student != null) {
			jsonBody = Utility.convertObjToString(student, context);
			context.getLogger().log("fetch Student By ID:::" + jsonBody);
			return createAPIResponse(jsonBody, 200, Utility.createHeaders());
		} else {
			jsonBody = "Student Not Found Exception :" + stdId;
			return createAPIResponse("Student Not Found Exception : " + jsonBody, 400, Utility.createHeaders());
		}

	}

	public APIGatewayProxyResponseEvent getStudents(APIGatewayProxyRequestEvent apiGatewayRequest, Context context) {
		initDynamoDB();
		List<Student> employees = dynamoDBMapper.scan(Student.class, new DynamoDBScanExpression());
		jsonBody = Utility.convertListOfObjToString(employees, context);
		context.getLogger().log("fetch employee List:::" + jsonBody);
		return createAPIResponse(jsonBody, 200, Utility.createHeaders());
	}

	public APIGatewayProxyResponseEvent deleteStudentById(APIGatewayProxyRequestEvent apiGatewayRequest,
			Context context) {
		initDynamoDB();
		String stdId = apiGatewayRequest.getPathParameters().get("stdId");
		Student student = dynamoDBMapper.load(Student.class, stdId);
		if (student != null) {
			dynamoDBMapper.delete(student);
			context.getLogger().log("data deleted successfully :::" + stdId);
			return createAPIResponse("Student details of " + stdId + " deleted successfully", 200,
					Utility.createHeaders());
		} else {
			jsonBody = "Student Not Found Exception :" + stdId;
			return createAPIResponse(jsonBody, 400, Utility.createHeaders());
		}
	}

	public APIGatewayProxyResponseEvent updateStudentById(APIGatewayProxyRequestEvent apiGatewayRequest,
			Context context) {
		initDynamoDB();

		String stdId = apiGatewayRequest.getPathParameters().get("stdId");

		Student existingStudent = dynamoDBMapper.load(Student.class, stdId);
		if (existingStudent == null) {
			String errorMessage = "Employee with ID " + stdId + " not found.";
			context.getLogger().log(errorMessage);
			return createAPIResponse(errorMessage, 404, Utility.createHeaders());
		}

		Student updatedStudent = Utility.convertStringToObj(apiGatewayRequest.getBody(), context);

		existingStudent.setStdId(updatedStudent.getStdId());
		existingStudent.setName(updatedStudent.getName());
		existingStudent.setEmail(updatedStudent.getEmail());
		existingStudent.setPhone(updatedStudent.getPhone());
		existingStudent.setCity(updatedStudent.getCity());
		dynamoDBMapper.save(existingStudent);

		String jsonBody = Utility.convertObjToString(existingStudent, context);
		context.getLogger().log("Student updated successfully: " + jsonBody);
		return createAPIResponse("Student updated successfully: " + jsonBody, 200, Utility.createHeaders());
	}

	private void initDynamoDB() {
		AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
		dynamoDBMapper = new DynamoDBMapper(client);
	}

	private APIGatewayProxyResponseEvent createAPIResponse(String body, int statusCode, Map<String, String> headers) {
		APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
		responseEvent.setBody(body);
		responseEvent.setHeaders(headers);
		responseEvent.setStatusCode(statusCode);
		return responseEvent;
	}

}
