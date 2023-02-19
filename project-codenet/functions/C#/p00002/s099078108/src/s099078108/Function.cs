using Amazon.Lambda.Core;
using Amazon.Lambda.RuntimeSupport;
using Amazon.Lambda.Serialization.SystemTextJson;

// The function handler that will be called for each Lambda event
var handler = (string input, ILambdaContext context) =>
{
    do
    {
        if (string.IsNullOrEmpty(input))
        {
            break;
        }
        string[] inputArr = input.Split(' ');
        int a = int.Parse(inputArr[0]);
        int b = int.Parse(inputArr[1]);
        int sum = a + b;
        return sum.ToString().Length;
    } while (true);
};

// Build the Lambda runtime client passing in the handler to call for each
// event and the JSON serializer to use for translating Lambda JSON documents
// to .NET types.
await LambdaBootstrapBuilder.Create(handler, new DefaultLambdaJsonSerializer())
        .Build()
        .RunAsync();