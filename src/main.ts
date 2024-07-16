import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';
import { Handler, Context, Callback } from 'aws-lambda';
import { ExpressAdapter } from '@nestjs/platform-express';
import * as express from 'express';
import * as awsServerlessExpress from 'aws-serverless-express';
import { Server } from 'http';

let server: Server;

async function bootstrap() {
  const expressApp = express();
  const adapter = new ExpressAdapter(expressApp);
  const app = await NestFactory.create(AppModule, adapter);
  await app.init();
  return awsServerlessExpress.createServer(expressApp);
}

export const handler: Handler = async (event: any, context: Context, callback: Callback) => {
  server = server ?? (await bootstrap());
  return awsServerlessExpress.proxy(server, event, context, 'PROMISE').promise;
};