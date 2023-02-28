FROM public.ecr.aws/lambda/python:3.8

# Copy application code into the container
COPY . ${LAMBDA_TASK_ROOT}

# Set the command to be run when the container starts
CMD ["main.handler"]
