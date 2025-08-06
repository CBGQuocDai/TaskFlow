# Giai đoạn 1: Xây dựng ứng dụng frontend
# Sử dụng base image node:20-alpine
FROM node:20-alpine AS builder

# Thiết lập thư mục làm việc bên trong container
WORKDIR /app
COPY package*.json ./

# Cài đặt các dependencies
RUN npm install

# Sao chép toàn bộ mã nguồn của ứng dụng
COPY . .

# Chạy lệnh build của ứng dụng
RUN npm run build

# Giai đoạn 2: Phục vụ ứng dụng đã build bằng Nginx
FROM nginx:alpine

# Xóa thư mục mặc định của Nginx để chuẩn bị sao chép file
RUN rm -rf /usr/share/nginx/html/*
COPY nginx.conf /etc/nginx/conf.d/default.conf
# Sao chép các file đã build từ giai đoạn 'builder' vào thư mục phục vụ của Nginx
COPY --from=builder /app/dist /usr/share/nginx/html
COPY entrypoint.sh /
RUN chmod +x /entrypoint.sh
# Mở cổng 80 để truy cập ứng dụng
EXPOSE 80
ENTRYPOINT ["/entrypoint.sh"]