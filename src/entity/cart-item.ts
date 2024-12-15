import { Entity, PrimaryColumn, Column, ManyToOne } from 'typeorm';
import { Cart } from './cart';

@Entity('cart_items')
export class CartItem {
    @PrimaryColumn('uuid')
    cartId: string;

    @PrimaryColumn('uuid')
    productId: string;

    @Column('int')
    count: number;

    @ManyToOne(() => Cart, cart => cart.items)
    cart: Cart;
}