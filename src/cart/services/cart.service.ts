import { Injectable } from '@nestjs/common';
import { v4 } from 'uuid';
import { Cart, CartStatuses } from '../models';

@Injectable()
export class CartService {
  private userCarts: Record<string, Cart> = {};

  findByUserId(userId: string): Cart {
    return this.userCarts[userId];
  }

  createByUserId(userId: string): Cart {
    const id = v4();
    const now = new Date().toISOString();
    const userCart: Cart = {
      id,
      user_id: userId,
      created_at: now,
      updated_at: now,
      status: CartStatuses.OPEN,
      items: [],
    } as Cart;

    this.userCarts[userId] = userCart;

    return userCart;
  }

  findOrCreateByUserId(userId: string): Cart {
    const userCart = this.findByUserId(userId);

    if (userCart) {
      return userCart;
    }

    return this.createByUserId(userId);
  }

  updateByUserId(userId: string, { items }: Cart): Cart {
    const { id, user_id, created_at, status } = this.findOrCreateByUserId(userId);

    const updatedCart: Cart = {
      id,
      user_id,
      created_at,
      updated_at: new Date().toISOString(),
      status,
      items: [...items],
    };

    this.userCarts[userId] = updatedCart;

    return updatedCart;
  }

  removeByUserId(userId: string): void {
    this.userCarts[userId] = null;
  }
}