package com.chikeandroid.debtmanager20.debtdetail;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.chikeandroid.debtmanager20.DebtManagerApplication;
import com.chikeandroid.debtmanager20.R;
import com.chikeandroid.debtmanager20.adddebt.AddEditDebtActivity;
import com.chikeandroid.debtmanager20.adddebt.AddEditDebtFragment;
import com.chikeandroid.debtmanager20.data.PersonDebt;
import com.chikeandroid.debtmanager20.databinding.FragmentDebtDetailBinding;
import com.chikeandroid.debtmanager20.util.StringUtil;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Chike on 4/20/2017.
 */

public class DebtDetailFragment extends Fragment implements DebtDetailContract.View {

    public static final String EXTRA_DEBT_ID = "com.chikeandroid.debtmanager20.debtdetail.DebtDetailFragment.extra_debt_id";
    public static final String ARG_DEBT_ID = "com.chikeandroid.debtmanager20.debtdetail.DebtDetailFragment.argument_debt_id";

    private PersonDebt mPersonDebt;
    private FragmentDebtDetailBinding mFragmentDebtDetailBinding;
    private ActionBar mActionBar;
    private String mDebtId;
    private View mView;
    private Toolbar mToolbar;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;

    @Inject
    DebtDetailPresenter mDebtDetailPresenter;

    private DebtDetailContract.Presenter mPresenter;

    public static DebtDetailFragment newInstance(String debtId) {
        Bundle args = new Bundle();
        args.putString(ARG_DEBT_ID, debtId);
        DebtDetailFragment debtDetailFragment = new DebtDetailFragment();
        debtDetailFragment.setArguments(args);
        return debtDetailFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mDebtId = getArguments().getString(ARG_DEBT_ID);
        }
        DaggerDebtDetailComponent.builder()
                .debtDetailPresenterModule(new DebtDetailPresenterModule(this, mDebtId))
                .applicationComponent(((DebtManagerApplication) getActivity().getApplication()).getComponent()).build()
                .inject(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mFragmentDebtDetailBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_debt_detail, container, false);

        FloatingActionButton fab = mFragmentDebtDetailBinding.fabScrolling;
        mCollapsingToolbarLayout = mFragmentDebtDetailBinding.collapsingToolbar;

        mCollapsingToolbarLayout.setTitle("");
        mView = mFragmentDebtDetailBinding.getRoot();

        mToolbar = mFragmentDebtDetailBinding.toolbar;
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        mActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setDisplayShowTitleEnabled(false);
        }

        return mView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_debt_detail, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                break;
            case R.id.action_edit:
                Intent intent = new Intent(getActivity(), AddEditDebtActivity.class);
                intent.putExtra(AddEditDebtFragment.ARGUMENT_EDIT_DEBT, mPersonDebt);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setPresenter(DebtDetailContract.Presenter presenter) {
        checkNotNull(presenter);
        mPresenter = presenter;
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public void openEditDebtUi() {

    }

    @Override
    public void deleteDebt() {

    }

    @Override
    public void callDebtor() {

    }

    @Override
    public void addPartialPayment() {

    }

    @Override
    public void showDebt(@NonNull PersonDebt personDebt) {
        checkNotNull(personDebt);
        mPersonDebt = personDebt;

        mFragmentDebtDetailBinding.setPersonDebt(mPersonDebt);
        mCollapsingToolbarLayout.setTitle(StringUtil.commaNumber(mPersonDebt.getDebt().getAmount()));
    }

    @Override
    public void showMissingDebt() {

    }
}
