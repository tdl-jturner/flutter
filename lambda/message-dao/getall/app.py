import boto3
from boto3.dynamodb.conditions import Key, Attr
from botocore.exceptions import ClientError

def lambda_handler(event, context):

    if not 'payload' in event:
        return {"StatusCode": 500,"body":"Payload not found"}

    try:

        table = boto3.resource('dynamodb').Table('Messages')

        if not 'author' in event['payload']:
            return {"StatusCode": 500,"body":"Author not found"}

        if 'since' in event:
            response = table.query(
                IndexName='MessagesByAuthorDateIndex',
                KeyConditionExpression=Key('author').eq(event['payload']['author']) &
                                       Key('createdDttm').gte(event['since'])
            )
        else:
            response = table.query(
                IndexName='MessagesByAuthorDateIndex',
                KeyConditionExpression=Key('author').eq(event['payload']['author'])
            )

    except ClientError as e:
        if e.response['Error']['Code'] == "ResourceNotFoundException":
            return {
                "statusCode": 404,
                "body": {
                    "error": "No record found"
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

        if len(response['Items']) > 0:
            return {
                "statusCode": 200,
                "body": {
                    "payload": response['Items']
                }
            }
        else:
            return {
                "statusCode": 404,
                "body": {
                    "error": "No record found"
                }
            }
