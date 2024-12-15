import { Entity, PrimaryGeneratedColumn, Column, CreateDateColumn, UpdateDateColumn, OneToMany } from 'typeorm';
import { CartStatus } from './cart-status';
import { CartItem } from './cart-item';

@Entity('carts')
export class Cart {
    @PrimaryGeneratedColumn('uuid')
    id: string;

    @Column('uuid')
    userId: string;

    @CreateDateColumn({ type: 'date', default: () => 'CURRENT_DATE' })
    createdAt: Date;

    @UpdateDateColumn({ type: 'date', default: () => 'CURRENT_DATE' })
    updatedAt: Date;

    @Column({
        type: 'enum',
        enum: CartStatus
    })
    status: CartStatus;

    @OneToMany(() => CartItem, cartItem => cartItem.cart)
    items: CartItem[];
}