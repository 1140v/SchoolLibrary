package com.read.schoolbook.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.read.schoolbook.R;
import com.read.schoolbook.activity.BorrowRecordListActivity;
import com.read.schoolbook.model.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;
    private OnUserActionListener listener;
    private User currentUser;
    private Context context;

    public interface OnUserActionListener {
        void onEditUser(User user);
        void onDeleteUser(User user);
    }

    public UserAdapter(List<User> userList, OnUserActionListener listener) {
        this.userList = userList;
        this.listener = listener;
        this.currentUser = null;
    }

    public UserAdapter(List<User> userList, OnUserActionListener listener, User currentUser) {
        this.userList = userList;
        this.listener = listener;
        this.currentUser = currentUser;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        this.context = parent.getContext();
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void updateData(List<User> newUserList) {
        this.userList = newUserList;
        notifyDataSetChanged();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView userNameText;
        private TextView userRoleText;
        private TextView permissionBadge;
        private TextView genderText;
        private TextView ageText;
        private TextView contactText;
        private TextView addressText;
        private MaterialButton viewBorrowRecordsButton;
        private MaterialButton editButton;
        private MaterialButton deleteButton;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            
            userNameText = itemView.findViewById(R.id.userNameText);
            userRoleText = itemView.findViewById(R.id.userRoleText);
            permissionBadge = itemView.findViewById(R.id.permissionBadge);
            genderText = itemView.findViewById(R.id.genderText);
            ageText = itemView.findViewById(R.id.ageText);
            contactText = itemView.findViewById(R.id.contactText);
            addressText = itemView.findViewById(R.id.addressText);
            viewBorrowRecordsButton = itemView.findViewById(R.id.viewBorrowRecordsButton);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }

        public void bind(User user) {
            userNameText.setText(user.getUsername());
            
            if (user.isAdmin()) {
                userRoleText.setText("管理员");
                userRoleText.setTextColor(userRoleText.getContext().getResources().getColor(R.color.title_color));
                permissionBadge.setText("管理员");
                permissionBadge.setBackgroundResource(R.drawable.gradient_button);
                permissionBadge.setTextColor(permissionBadge.getContext().getResources().getColor(android.R.color.white));
            } else {
                userRoleText.setText("普通用户");
                userRoleText.setTextColor(userRoleText.getContext().getResources().getColor(android.R.color.black));
                permissionBadge.setText("普通用户");
                permissionBadge.setBackgroundResource(R.drawable.rounded_input_box);
                permissionBadge.setTextColor(permissionBadge.getContext().getResources().getColor(android.R.color.black));
            }
            
            genderText.setText(user.getGender());
            ageText.setText(String.valueOf(user.getAge()));
            contactText.setText(user.getContact());
            addressText.setText(user.getAddress());

            // 根据当前用户权限控制按钮显示
            if (currentUser != null && currentUser.isAdmin()) {
                // 管理员：显示所有按钮
                viewBorrowRecordsButton.setVisibility(View.VISIBLE);
                editButton.setVisibility(View.VISIBLE);
                deleteButton.setVisibility(View.VISIBLE);
                
                // 查看借阅记录按钮点击事件
                viewBorrowRecordsButton.setOnClickListener(v -> {
                    // 跳转到用户的借阅记录页面
                    Intent intent = new Intent(context, BorrowRecordListActivity.class);
                    intent.putExtra("selectedUser", user);
                    intent.putExtra("currentUser", currentUser);
                    context.startActivity(intent);
                });
                
                // 编辑按钮点击事件
                editButton.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onEditUser(user);
                    }
                });

                // 删除按钮点击事件
                deleteButton.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onDeleteUser(user);
                    }
                });
            } else {
                // 普通用户：隐藏编辑和删除按钮
                viewBorrowRecordsButton.setVisibility(View.GONE);
                editButton.setVisibility(View.GONE);
                deleteButton.setVisibility(View.GONE);
            }
        }
    }
}