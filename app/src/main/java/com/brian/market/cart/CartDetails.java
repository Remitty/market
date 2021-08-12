package com.brian.market.cart;

import com.brian.market.models.ProductDetails;

public class CartDetails {
    private ProductDetails cartProduct;
    private int cartID;

    public void setCartID(int cartID) {
        this.cartID = cartID;
    }

    public ProductDetails getCartProduct() {
        return cartProduct;
    }

    public void setCartProduct(ProductDetails cartProduct) {
        this.cartProduct = cartProduct;
    }


}
