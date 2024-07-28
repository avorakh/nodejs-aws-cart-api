import { Module } from '@nestjs/common';

import { AppController } from './app.controller';
import { Cart } from './entity'
import { CartModule } from './cart/cart.module';
import { AuthModule } from './auth/auth.module';
import { OrderModule } from './order/order.module';

import { TypeOrmModule } from '@nestjs/typeorm';

@Module({
  imports: [
    AuthModule,
    CartModule,
    OrderModule,
  ],
  controllers: [
    AppController,
  ],
  providers: [],
})
export class AppModule {}
