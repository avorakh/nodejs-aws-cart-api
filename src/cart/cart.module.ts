import { Module } from '@nestjs/common';

import { OrderModule } from '../order/order.module';
import { TypeOrmModule } from '@nestjs/typeorm';
import { Cart, CartItem } from './../entity'
import { CartController } from './cart.controller';
import { CartService } from './services';


@Module({
  imports: [ OrderModule, TypeOrmModule.forFeature([Cart, CartItem])],
  providers: [ CartService ],
  controllers: [ CartController ]
})
export class CartModule {}
