import boto3
from boto3.dynamodb.conditions import Key, Attr
from botocore.exceptions import ClientError


def lambda_handler(event, context):
    """Flutter: MessageDao->Get Handler

    Parameters
    ----------
    event: dict, required
        API Gateway Lambda Proxy Input Format

        Event doc: https://docs.aws.amazon.com/apigateway/latest/developerguide/set-up-lambda-proxy-integrations.html#api-gateway-simple-proxy-for-lambda-input-format

    context: object, required
        Lambda Context runtime methods and attributes

        Context doc: https://docs.aws.amazon.com/lambda/latest/dg/python-context-object.html

    event['payload']['id'] string, required
        Payload object containing id of message

    Returns
    ------
    API Gateway Lambda Proxy Output Format: dict

        Return doc: https://docs.aws.amazon.com/apigateway/latest/developerguide/set-up-lambda-proxy-integrations.html

    body['payload'] object, optional
        Result of query if available

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

    try:
        response = boto3.resource('dynamodb').Table('Message').get_item(
            Key={
                'id': event['payload']['id']
            }
        )
    except ClientError as e:
        if e.response['Error']['Code'] == "ResourceNotFoundException":
            return {
                "statusCode": 404,
                "body": {
                    "error": "No data found"
                }
            }
        else:
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
                "payload": response['Item']
            }
        }
