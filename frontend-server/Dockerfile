# Base Image 
FROM python:3.8

# Working directory inside app
WORKDIR /app

#Copy the index.html file /usr/share/nginx/html/
COPY . /app

# install pip packages
RUN pip install -r packages.txt

# expose server port
EXPOSE 5000

# start server 
ENTRYPOINT ["python"]
CMD ["server.py"]