   import { Entity, PrimaryGeneratedColumn, Column, CreateDateColumn, UpdateDateColumn } from 'typeorm';

   export enum CartStatus {
     OPEN = 'OPEN',
     ORDERED = 'ORDERED',
   }

   @Entity('carts')
   export class Cart {
     @PrimaryGeneratedColumn('uuid')
     id: string;

     @Column('uuid')
     user_id: string;

     @CreateDateColumn({ type: 'date' })
     created_at: Date;

     @UpdateDateColumn({ type: 'date' })
     updated_at: Date;

     @Column({
       type: 'enum',
       enum: CartStatus,
     })
     status: CartStatus;
   }