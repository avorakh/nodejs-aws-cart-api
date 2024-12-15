import { NestFactory } from '@nestjs/core';
import { ExpressAdapter } from '@nestjs/platform-express';
import serverlessExpress from '@vendia/serverless-express';
import { Callback, Context, Handler } from 'aws-lambda';
import helmet from 'helmet';
import express from 'express';

import { AppModule } from './app.module';

const port = process.env.PORT || 4000;
let cachedServer: Handler;

async function bootstrap() {
  if (!cachedServer) {
    const expressApp = express();
    const app = await NestFactory.create(AppModule, new ExpressAdapter(expressApp));

    app.enableCors({
      origin: (req, callback) => callback(null, true),
    });

    app.use(helmet());
    await app.init();

    cachedServer = serverlessExpress({ app: expressApp });
  }

  return cachedServer;
}

bootstrap().then(() => {
  console.log('App is running on %s port', port);
});

export const handler = async (event: any, context: Context, callback: Callback) => {
  console.log('event: ', event);

  const server = await bootstrap();
  return server(event, context, callback);
};