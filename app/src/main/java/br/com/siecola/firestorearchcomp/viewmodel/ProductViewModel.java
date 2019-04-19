package br.com.siecola.firestorearchcomp.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import br.com.siecola.firestorearchcomp.model.Product;
import br.com.siecola.firestorearchcomp.repository.ProductRepository;

public class ProductViewModel extends ViewModel {
    private MutableLiveData<Product> product;
    private MutableLiveData<List<Product>> products;

    public ProductViewModel(){}

    public MutableLiveData<Product> getProductById(String productId) {
        if (product == null) {
            product = ProductRepository.getInstance().getProductById(productId);
        }
        return product;
    }

    public MutableLiveData<List<Product>> getAllProducts() {
        if (products == null) {
            products = ProductRepository.getInstance().getProducts();
        }
        return products;
    }

    public void clearProducts() {
        products = null;
    }
}
