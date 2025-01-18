import { useAdminAuth } from "@/contexts/AdminAuthContext";
import { Button, Form, Input, Typography, message } from "antd";
import axios from "axios";

import { useState } from "react";

import { useRouter } from "next/navigation";

import { getApiBaseUrl } from "@/utils/getApiBaseUrl";

const { Title } = Typography;

const AdminLogin = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const router = useRouter();
  const { login } = useAdminAuth();

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const response = await axios.post(`${getApiBaseUrl()}/admin/login`, {
        username,
        password,
      });

      if (response.data.token) {
        login(response.data.token);
        router.push("/admin");
      }
    } catch (error) {
      // エラーハンドリングの改善
      if (axios.isAxiosError(error)) {
        message.error(error.response?.data?.message || "ログインに失敗しました");
      } else {
        message.error("予期せぬエラーが発生しました");
      }
    }
  };

  return (
    <div
      style={{
        maxWidth: "400px",
        margin: "auto",
        padding: "40px",
        textAlign: "center",
        color: "#d3d3d3",
      }}
    >
      <Title level={2} style={{ color: "#d3d3d3" }}>
        Admin Login
      </Title>
      <Form onSubmitCapture={handleLogin} layout="vertical">
        <Form.Item label={<span style={{ color: "#d3d3d3" }}>Username</span>} required>
          <Input
            placeholder="Enter your username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            style={{ color: "#d3d3d3", backgroundColor: "#333", borderColor: "#444" }}
          />
        </Form.Item>
        <Form.Item label={<span style={{ color: "#d3d3d3" }}>Password</span>} required>
          <Input.Password
            placeholder="Enter your password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            style={{ color: "#d3d3d3", backgroundColor: "#333", borderColor: "#444" }}
          />
        </Form.Item>
        <Form.Item>
          <Button
            type="primary"
            htmlType="submit"
            style={{ width: "100%", backgroundColor: "#1a73e8", borderColor: "#1a73e8" }}
          >
            Login
          </Button>
        </Form.Item>
      </Form>
    </div>
  );
};

export default AdminLogin;
