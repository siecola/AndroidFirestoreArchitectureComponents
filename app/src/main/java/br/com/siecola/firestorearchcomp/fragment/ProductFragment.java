package br.com.siecola.firestorearchcomp.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import br.com.siecola.firestorearchcomp.R;
import br.com.siecola.firestorearchcomp.model.Product;
import br.com.siecola.firestorearchcomp.repository.ProductRepository;
import br.com.siecola.firestorearchcomp.util.GlobalArgs;
import br.com.siecola.firestorearchcomp.viewmodel.ProductViewModel;

public class ProductFragment extends Fragment {

    private View rootView;

    private EditText edtName;
    private EditText edtCode;
    private EditText edtDescription;
    private EditText edtPrice;

    private String productId;

    private ProductViewModel productViewModel;

    private Product product;

    public ProductFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        edtName = rootView.findViewById(R.id.edtName);
        edtCode = rootView.findViewById(R.id.edtCode);
        edtDescription = rootView.findViewById(R.id.edtDescription);
        edtPrice = rootView.findViewById(R.id.edtPrice);

        Bundle arguments = getArguments();
        if ((arguments != null) && arguments.containsKey(GlobalArgs.PRODUCT_ID)) {
            getActivity().setTitle("Edit product");
            productId = arguments.getString(GlobalArgs.PRODUCT_ID);
        } else{
            getActivity().setTitle("New product");
        }

        productViewModel = ViewModelProviders.of(this).get(ProductViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_product, container, false);

        ConstraintLayout layout = rootView.findViewById(R.id.layout);
        layout.setOnClickListener(v -> hideKeyboard());

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (productId != null) {
            productViewModel.getProductById(productId).observe(this, product -> {
                this.product = product;
                if (product != null) {
                    edtName.setText (product.getName());
                    edtCode.setText (product.getCode());
                    edtDescription.setText (product.getDescription());
                    edtPrice.setText (String.valueOf(product.getPrice()));
                }
            });
        }
    }

    @Override
    public void onPause() {
        if (!edtName.getText().toString().isEmpty()) {
            if (product == null) {
                product = new Product();
                product.setId(null);
            }
            product.setName(edtName.getText().toString());
            product.setCode(edtCode.getText().toString());
            product.setDescription(edtDescription.getText().toString());

            if (edtPrice.getText().toString().isEmpty()) {
                edtPrice.setText("0");
            }

            product.setPrice(Double.parseDouble(edtPrice.getText().toString()));

            ProductRepository.getInstance().saveProduct(product);
        } else {
            Toast.makeText(getActivity(), "This product was not saved!", Toast.LENGTH_SHORT).show();
        }
        super.onPause();
    }

    private void hideKeyboard() {
        if (getActivity() != null) {
            InputMethodManager imm = (InputMethodManager)getActivity()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if ((imm != null) && (getActivity().getCurrentFocus() != null) &&
                    (getActivity().getCurrentFocus().getWindowToken() != null)) {
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
            }
        }
    }
}
