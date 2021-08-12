package com.brian.market.cart;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.brian.market.R;
import com.brian.market.models.ProductDetails;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import static com.brian.market.App.getContext;

public class CartItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private CartFragment cartFragment;
//    private Checkout checkoutFragment;
    private List<ProductDetails> cartItemsList;

    private static int LAYOUT_ONE = 0;
    int number = 1;

    public CartItemAdapter(Context context, List<ProductDetails> cartItemsList, CartFragment cartFragment) {
        this.context = context;
        this.cartItemsList = cartItemsList;
        this.cartFragment = cartFragment;

        LAYOUT_ONE = 0;
    }

//    public CartItemAdapter(Context context, List<ProductDetails> cartItemsList, Checkout checkoutFragment) {
//        this.context = context;
//        this.cartItemsList = cartItemsList;
//        this.checkoutFragment = checkoutFragment;
//
//        LAYOUT_ONE = 1;
//    }


    //********** Called to Inflate a Layout from XML and then return the Holder *********//

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

        View view = null;
        RecyclerView.ViewHolder viewHolder = null;

        if (LAYOUT_ONE == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
            viewHolder = new MyViewHolder0(view);
        } else {
//            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_card_item_checkout, parent, false);
//            viewHolder = new MyViewHolder2(view);
        }

        return viewHolder;

    }


    //********** Called by RecyclerView to display the Data at the specified Position *********//

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holderAll, int position) {
        // Get the data model based on Position
        final ProductDetails cartDetails = cartItemsList.get(position);

        if (LAYOUT_ONE == 0) {

            final MyViewHolder0 holder = (MyViewHolder0) holderAll;

            String product_qty = ""+cartFragment.GetItemQTY(cartDetails.getId());
            holder.cart_item_title.setText(cartDetails.getCardName());
//            holder.cart_item_category.setText(cartDetails.getCategoryNames());
            holder.cart_item_quantity.setText(product_qty);
            holder.cart_item_base_price.setText(new DecimalFormat("$#0.00").format(Double.parseDouble(cartDetails.getPrice())));
            holder.cart_item_sub_price.setText(new DecimalFormat("$#0.00").format(Double.parseDouble(cartDetails.getPrice()) * Double.parseDouble(product_qty)));
//        holder.cart_item_total_price.setText(ConstantValues.CURRENCY_SYMBOL + new DecimalFormat("#0.00").format(Double.parseDouble(cartDetails.getCartProduct().getTotalPrice())));
            Picasso.with(context).load(cartDetails.getImageResourceId())
//                    .error(R.drawable.ic_placeholder)
//                    .placeholder(R.drawable.ic_)
                    .into(holder.cart_item_cover);

            // Decrease Product Quantity

            holder.cart_item_quantity_minusBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Holds Product Quantity

//                    number = CartFragment.GetItemQTY(cartDetails.getId());

                    // Check if the Quantity is greater than the minimum Quantity
                    if (number > 1) {
                        // Decrease Quantity by 1
                        number = number - 1;
                        holder.cart_item_quantity.setText("" + number);

                        // Calculate Product Price with selected Quantity
                        double price = Double.parseDouble(cartDetails.getPrice()) * number;

                        // Set Final Price and Quantity
                        cartDetails.setTotalPrice("" + price);
//                        cartDetails.setProductsFinalPrice("" + price);
                        //cartDetails.setCustomersBasketQuantity(number[0]);


                        // Update CartItem in Local Database using static method of My_Cart

                        cartFragment.UpdateCartItemQty(cartDetails.getId(), number);
                       /* My_Cart.UpdateCartItem
                                (
                                        cartDetails
                                );*/


                        // cartFragment.addCartProducts(cartDetails);

                        notifyItemChanged(holder.getAdapterPosition());


                        // Calculate Cart's Total Price Again
                        cartFragment.updateCart();
                    }
                }
            });


            // Increase Product Quantity
            holder.cart_item_quantity_plusBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Check if the Quantity is less than the maximum or stock Quantity
                    if ( number < cartDetails.getQty()) {
                        // Increase Quantity by 1
                        number = CartFragment.GetItemQTY(cartDetails.getId());
                        number = number + 1;
                        holder.cart_item_quantity.setText("" + number);

                        // Calculate Product Price with selected Quantity
                        double price = Double.parseDouble(cartDetails.getPrice()) * number;

                        // Set Final Price and Quantity
                        cartDetails.setTotalPrice("" + price);
//                        cartDetails.setProductsFinalPrice("" + price);
                        cartDetails.setCustomersBasketQuantity(number);


                        // Update CartItem in Local Database using static method of My_Cart
                        cartFragment.UpdateCartItemQty(cartDetails.getId(), number);
                       /* My_Cart.UpdateCartItem
                                (
                                        cartDetails
                                );*/

                        // cartFragment.addCartProducts(cartDetails);

                        notifyItemChanged(holder.getAdapterPosition());


                        // Calculate Cart's Total Price Again
                        cartFragment.updateCart();
                    }
                    else {
                        Toast.makeText(getContext(), context.getString(R.string.here_is_max), Toast.LENGTH_SHORT).show();
                    }
                }
            });


            // View Product Details
            holder.cart_item_viewBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String productID = cartDetails.getId();

                   /* // Get Product Info
                    Bundle itemInfo = new Bundle();
                    itemInfo.putParcelable("productDetails", cartDetails.getCartProduct());*/

                    // Get Product Info
                    Bundle itemInfo = new Bundle();
                    itemInfo.putString("itemID", productID);


                    // Navigate to Product_Description of selected Product
//                    Fragment fragment = new Product_Description();
//                    fragment.setArguments(itemInfo);
//                    FragmentManager fragmentManager = ((MainActivity) context).getSupportFragmentManager();
//                    fragmentManager.beginTransaction()
//                            .replace(R.id.main_fragment, fragment)
//                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//                            .commit();
//
//
//                    // Add the Product to User's Recently Viewed Products
//                    if (!recents_db.getUserRecents().contains(cartDetails.getId())) {
//                        recents_db.insertRecentItem(cartDetails.getId());
//                    }
                }
            });


            // Delete relevant Product from Cart
            holder.cart_item_removeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(context.getResources().getString(R.string.app_name))
                            .setIcon(R.mipmap.ic_launcher)
                            .setMessage(context.getString(R.string.message_delete_product));
                    builder.setCancelable(true);
                    builder.setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            holder.cart_item_removeBtn.setClickable(false);

                            // Delete CartItem from Local Database using static method of My_Cart
                            cartFragment.DeleteCartItemID(cartDetails.getId());
                            cartFragment.DeleteCartItem(cartDetails.getId());

                            // Remove CartItem from Cart List
                            cartItemsList.remove(holder.getAdapterPosition());

                            // Notify that item at position has been removed
                            notifyItemRemoved(holder.getAdapterPosition());

                            // Calculate Cart's Total Price Again
                            cartFragment.updateCart();

                            // Update Cart View from Local Database using static method of My_Cart
                            cartFragment.updateCartView(getItemCount());
                        }
                    });
                    builder.setNegativeButton(context.getString(R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });

        }
    }


    //********** Returns the total number of items in the data set *********//

    @Override
    public int getItemCount() {
        return cartItemsList.size();
    }


    /********** Custom ViewHolder provides a direct reference to each of the Views within a Data_Item *********/

    public class MyViewHolder0 extends RecyclerView.ViewHolder {

        private Button cart_item_viewBtn;
        private ImageView cart_item_cover;
        private RecyclerView attributes_recycler;
        private ImageButton cart_item_quantity_minusBtn, cart_item_quantity_plusBtn, cart_item_removeBtn;
        private TextView cart_item_title, cart_item_category, cart_item_base_price, cart_item_sub_price, cart_item_total_price, cart_item_quantity;
        ProgressBar cover_loader;

        public MyViewHolder0(final View itemView) {
            super(itemView);
//            cover_loader = (ProgressBar) itemView.findViewById(R.id.product_cover_loader);
            cart_item_title = (TextView) itemView.findViewById(R.id.cart_item_title);
            cart_item_base_price = (TextView) itemView.findViewById(R.id.cart_item_base_price);
            cart_item_sub_price = (TextView) itemView.findViewById(R.id.cart_item_sub_price);
            cart_item_total_price = (TextView) itemView.findViewById(R.id.cart_item_total_price);
            cart_item_quantity = (TextView) itemView.findViewById(R.id.cart_item_quantity);
            cart_item_category = (TextView) itemView.findViewById(R.id.cart_item_category);
            cart_item_cover = (ImageView) itemView.findViewById(R.id.cart_item_cover);
            cart_item_viewBtn = (Button) itemView.findViewById(R.id.cart_item_viewBtn);
            cart_item_removeBtn = (ImageButton) itemView.findViewById(R.id.cart_item_removeBtn);
            cart_item_quantity_plusBtn = (ImageButton) itemView.findViewById(R.id.cart_item_quantity_plusBtn);
            cart_item_quantity_minusBtn = (ImageButton) itemView.findViewById(R.id.cart_item_quantity_minusBtn);

//            attributes_recycler = (RecyclerView) itemView.findViewById(R.id.cart_item_attributes_recycler);


            cart_item_total_price.setVisibility(View.GONE);
        }

    }

}
