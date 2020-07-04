#!/bin/sh

errorMsg="Failed to contact Development Server Builder."

echo "Informing development server builder to deploy new version"
code=$(curl -I 139.162.149.158:16542/build-deploy | head -n 1 | cut -d ' ' -f2)

echo "response: $code"

if [ -z "$code" ]
then
  echo "$errorMsg"
  exit 1
fi

if [ $code -eq 200 ]
then
  echo "Development Server Builder successfully got the request to re-deploy"
else
  echo "$errorMsg"
  exit 1
fi
