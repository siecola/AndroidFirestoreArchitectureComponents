package br.com.siecola.firestorearchcomp.fragment;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import br.com.siecola.firestorearchcomp.R;
import br.com.siecola.firestorearchcomp.adapter.ProductAdapter;
import br.com.siecola.firestorearchcomp.model.Product;
import br.com.siecola.firestorearchcomp.repository.ProductRepository;
import br.com.siecola.firestorearchcomp.util.GlobalArgs;
import br.com.siecola.firestorearchcomp.viewmodel.ProductViewModel;

import static android.support.v7.widget.RecyclerView.VERTICAL;

public class ProductsListFragment extends Fragment implements ProductAdapter.OnProductSelectedListener {

    private static String TAG = "ProductsListFragment";

    private ProductAdapter productAdapter;
    private ProductViewModel productViewModel;
    private ActionMode actionMode;

    private Product productSelected;

    public ProductsListFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_products_list, container, false);
        getActivity().setTitle(R.string.str_products);

        productViewModel = ViewModelProviders.of(this).get(ProductViewModel.class);

        RecyclerView rcvProducts = rootView.findViewById(R.id.rcvProducts);
        productAdapter = new ProductAdapter(this);
        rcvProducts.setLayoutManager(new LinearLayoutManager(getActivity()));
        rcvProducts.setAdapter(productAdapter);

        DividerItemDecoration itemDecor = new DividerItemDecoration(getContext(), VERTICAL);
        rcvProducts.addItemDecoration(itemDecor);

        FloatingActionButton fab = rootView.findViewById(R.id.btnAddProduct);
        fab.setOnClickListener(view -> startProductDetail(null));

        hideKeyboard();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        productViewModel.getAllProducts().observe(this, products -> {
            productAdapter.setProducts(products);
        });
    }

    @Override
    public void onProductSelected(String productId) {
        if (actionMode == null) {
            startProductDetail(productId);
        }
    }

    @Override
    public boolean onProductLongSelected(Product product) {
        if (actionMode != null) {
            return false;
        }

        productSelected = product;
        actionMode = getActivity().startActionMode(mActionModeCallback);
        return true;
    }

    private void startProductDetail(String productId) {
        Class fragmentClass;
        Fragment fragment = null;

        fragmentClass = ProductFragment.class;

        try {
            fragment = (Fragment) fragmentClass.newInstance();

            if ((productId != null) && (!productId.isEmpty())) {
                Bundle args = new Bundle();
                args.putString(GlobalArgs.PRODUCT_ID, productId);
                fragment.setArguments(args);
            }

            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction transaction =
                    fragmentManager.beginTransaction();

            transaction.replace(R.id.container, fragment,
                    ProductFragment.class.getCanonicalName());
            transaction.addToBackStack(
                    ProductsListFragment.class.getCanonicalName());

            transaction.commit();
        } catch (Exception e) {
            try {
                Toast.makeText(getActivity(),
                        "Error while trying to open product details",
                        Toast.LENGTH_SHORT).show();
            } catch (Exception e1) {}
        }
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            if (productSelected != null) {
                mode.setTitle(productSelected.getName());
            }
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.products_cab_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.product_edit:
                    if (productSelected != null) {
                        startProductDetail(productSelected.getId());
                    }
                    productSelected = null;
                    mode.finish();
                    return true;
                case R.id.product_delete:
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                    builder.setMessage(R.string.str_want_delete_product)
                            .setCancelable(true)
                            .setTitle(R.string.str_delete_product)
                            .setNegativeButton(R.string.str_no, null)
                            .setPositiveButton(R.string.str_yes, (dialog, id) -> {
                                if (productSelected != null) {
                                    ProductRepository.getInstance().deleteProduct(productSelected.getId());
                                }
                                productSelected = null;
                            });
                    AlertDialog alert = builder.create();
                    alert.show();

                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
        }
    };

    private void hideKeyboard() {
        if (getActivity() != null) {
            InputMethodManager imm = (InputMethodManager) getActivity()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if ((imm != null) && (getActivity().getCurrentFocus() != null) &&
                    (getActivity().getCurrentFocus().getWindowToken() != null)) {
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
            }
        }
    }
}
