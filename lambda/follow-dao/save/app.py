import uuid
import boto3
import datetime
from boto3.dynamodb.conditions import Key, Attr
from botocore.exceptions import ClientError


def lambda_handler(event, context):
    """Flutter: MessageDao->Save Handler

        Parameters
        ----------
        event: dict, required
            API Gateway Lambda Proxy Input Format

            Event doc: https://docs.aws.amazon.com/apigateway/latest/developerguide/set-up-lambda-proxy-integrations.html#api-gateway-simple-proxy-for-lambda-input-format

        context: object, required
            Lambda Context runtime methods and attributes

            Context doc: https://docs.aws.amazon.com/lambda/latest/dg/python-context-object.html

        event['payload']['id'] uuid, optional
            Message UUID, will be automatically created if missing

        event['payload']['message'] uuid, required
            Message content

        event['payload']['author'] uuid, required
            UUID of Message's author

        event['payload']['createdDttm'] timestamp, required
            CreateDTTM of message, will be set to current time if missing

        Returns
        ------
        API Gateway Lambda Proxy Output Format: dict

            Return doc: https://docs.aws.amazon.com/apigateway/latest/developerguide/set-up-lambda-proxy-integrations.html

        body['payload'] object, optional
            Saved object

        body['error'] string, optional
            Error message if status code 500


        Status Codes
        ------
        200: OK
        404: No Data Found
        500: Invalid request
        """
    if 'payload' not in event:
        return {
            "StatusCode": 500,
            "body": "Payload not found"
        }

    if 'author' not in event['payload']:
        return {
            "StatusCode": 500,
            "body": "Author not found"
        }

    if 'message' not in event['payload']:
        return {
            "StatusCode": 500,
            "body": "Message not found"
        }

    try:
        message = {
            "id": (event['payload']['id'] if 'id' in event['payload'] else str(uuid.uuid4())),
            "message": event['payload']['message'],
            "author": event['payload']['author'],
            "createdDttm": (event['payload']['createdDttm'] if 'createdDttm' in event['payload'] else int(datetime.datetime.utcnow().timestamp()))
        }
        boto3.resource('dynamodb').Table('Messages').put_item(Item=message)
    except ClientError as e:
        return {
            "statusCode": 500,
            "body": {
                "error": e.response['Error']
            }
        }
    else:
        return {
            "statusCode": 200,
            "body": {
                "payload": message
            }
        }
