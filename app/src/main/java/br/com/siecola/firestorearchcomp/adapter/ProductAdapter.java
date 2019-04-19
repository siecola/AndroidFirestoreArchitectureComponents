package br.com.siecola.firestorearchcomp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.com.siecola.firestorearchcomp.R;
import br.com.siecola.firestorearchcomp.model.Product;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private List<Product> products;

    public interface OnProductSelectedListener {
        void onProductSelected(String productId);
        boolean onProductLongSelected(Product product);
    }

    private ProductAdapter.OnProductSelectedListener listener;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtProductListItemName;
        TextView txtProductListItemCode;
        TextView txtProductListItemPrice;

        ViewHolder(View itemView) {
            super(itemView);
            txtProductListItemName = itemView.findViewById(R.id.txtProductListItemName);
            txtProductListItemCode = itemView.findViewById(R.id.txtProductListItemCode);
            txtProductListItemPrice = itemView.findViewById(R.id.txtProductListItemPrice);
        }
    }

    public ProductAdapter(ProductAdapter.OnProductSelectedListener listener) {
        this.listener = listener;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
        notifyDataSetChanged();
    }

    @Override
    public ProductAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                        int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View inflate = inflater.inflate(R.layout.item_product, parent, false);

        return new ProductAdapter.ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(ProductAdapter.ViewHolder holder, int position) {
        if (this.products != null) {
            Product product = this.products.get(position);

            holder.txtProductListItemName.setText(product.getName());
            holder.txtProductListItemCode.setText(product.getCode());
            holder.txtProductListItemPrice.setText(String.format("$ %s", product.getPrice()));

            holder.itemView.setOnClickListener(view -> {
                if (listener != null) {
                    listener.onProductSelected(product.getId());
                }
            });

            holder.itemView.setOnLongClickListener(view -> listener == null ||
                    listener.onProductLongSelected(product));
        }
    }

    @Override
    public int getItemCount() {
        if (this.products != null) {
            return this.products.size();
        } else {
            return 0;
        }
    }

}
