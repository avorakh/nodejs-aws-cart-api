import { NestFactory } from '@nestjs/core';
import { Callback, Context, Handler } from 'aws-lambda';
import serverlessExpress from '@vendia/serverless-express';

import helmet from 'helmet';

import { AppModule } from './app.module';

const port = process.env.PORT || 4000;
let server: Handler;

async function bootstrap() {
  console.log('START');
  const app = await NestFactory.create(AppModule);

  app.enableCors({
    origin: (req, callback) => callback(null, true),
  });
  app.use(helmet());

  // await app.listen(port);

  console.log('Initialization');

  await app.init();
  const expressApp = app.getHttpAdapter().getInstance();
  return serverlessExpress({ app: expressApp });
}

bootstrap().then(() => {
  // console.log('App is running on %s port', port);
  console.log('App is running');
});

export const handler: Handler = async (
  event: any,
  context: Context,
  callback: Callback,
) => {
  server = server ?? (await bootstrap());

  return server(event, context, callback);
};