import boto3
from boto3.dynamodb.conditions import Key, Attr
from botocore.exceptions import ClientError

def lambda_handler(event, context):

    if not 'payload' in event:
        return {"StatusCode": 500,"body":"Payload not found"}

    try:
        response = boto3.resource('dynamodb').Table('Messages').get_item(
            Key={
                'id': event['payload']['id']
            }
        )

    except ClientError as e:
        if e.response['Error']['Code'] == "ResourceNotFoundException":
            return {
                "statusCode": 404,
                "body": {
                    "error":"No record found"
                }
            }
        else:
            return {
                "statusCode": 500,
                "body": {
                    "error": e.response['Error']
                }
            }
        print(e.response['Error']['Message'])
    else:
        item = response['Item']

        return {
            "statusCode": 200,
            "body": {
                "payload": item
            }
        }
