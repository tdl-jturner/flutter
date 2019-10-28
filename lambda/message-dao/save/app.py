import uuid
import boto3
import datetime
from boto3.dynamodb.conditions import Key, Attr
from botocore.exceptions import ClientError

def lambda_handler(event, context):

    if not 'payload' in event:
        return {"StatusCode": 500,"body": "Payload not found"}

    if not 'author' in event['payload']:
        return {"StatusCode": 500,"body": "Author not found"}

    if not 'message' in event['payload']:
        return {"StatusCode": 500,"body": "Message not found"}

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
