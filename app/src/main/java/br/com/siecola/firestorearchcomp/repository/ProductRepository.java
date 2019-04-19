package br.com.siecola.firestorearchcomp.repository;

import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

import br.com.siecola.firestorearchcomp.model.Product;

public class ProductRepository {
    private static String TAG = "ProductRepository";
    private static FirebaseAuth mFirebaseAuth;
    private FirebaseFirestore mFirestore;

    private static ProductRepository instance = null;

    private ProductRepository(){
        mFirestore = FirebaseFirestore.getInstance();
    }

    public static synchronized ProductRepository getInstance() {
        if (instance == null) {
            instance = new ProductRepository();
            mFirebaseAuth = FirebaseAuth.getInstance();
        }
        return instance;
    }

    public MutableLiveData<List<Product>> getProducts() {
        MutableLiveData<List<Product>> liveProducts = new MutableLiveData<List<Product>>();

        mFirestore.collection(Product.COLLECTION)
                .whereEqualTo(Product.FIELD_userId, mFirebaseAuth.getUid())
                .orderBy(Product.FIELD_name, Query.Direction.ASCENDING)
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    List<Product> products = new ArrayList<>();
                    if (snapshot != null && !snapshot.isEmpty()) {
                        for (DocumentSnapshot documentSnapshot : snapshot.getDocuments()) {
                            Product product = documentSnapshot.toObject(Product.class);
                            product.setId(documentSnapshot.getId());
                            products.add(product);
                        }
                    }
                    liveProducts.postValue(products);
                });

        return liveProducts;
    }

    public MutableLiveData<Product> getProductById(String productId) {
        MutableLiveData<Product> liveProject = new MutableLiveData<>();

        final DocumentReference docRef = mFirestore.collection(Product.COLLECTION).document(productId);
        docRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                Product product = snapshot.toObject(Product.class);
                product.setId(snapshot.getId());
                liveProject.postValue(product);
            } else {
                Log.d(TAG, "Current data: null");
            }
        });

        return liveProject;
    }

    public void saveProduct(Product product)  {
        mFirestore.collection(Product.COLLECTION)
                .whereEqualTo(Product.FIELD_userId, mFirebaseAuth.getUid())
                .whereEqualTo(Product.FIELD_code, product.getCode())
                .limit(1)
                .get().addOnCompleteListener(task -> {
                    boolean existCode = false;
                    if (task.isSuccessful()) {
                        if (task.getResult().getDocuments().size() > 0) {
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                            existCode = (product.getId() == null) || (!product.getId().equals(document.getId()));
                        }
                        if (!existCode) {
                            DocumentReference document;
                            if (product.getId() != null) {
                                document = mFirestore.collection(Product.COLLECTION).document(product.getId());
                            } else {
                                product.setUserId(mFirebaseAuth.getUid());
                                document = mFirestore.collection(Product.COLLECTION).document();
                            }
                            document.set(product);
                            product.setId(document.getId());
                        }
                    }
                });
    }

    public void deleteProduct(String productId) {
        final DocumentReference docRef = mFirestore.collection(Product.COLLECTION).document(productId);
        docRef.delete();
    }
}
