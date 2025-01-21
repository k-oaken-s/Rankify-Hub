"use client";

import { useAdminAuth } from "@/contexts/AdminAuthContext";

import { useEffect } from "react";

import { usePathname, useRouter } from "next/navigation";

export default function AdminLayout({ children }: { children: React.ReactNode }) {
  const { isAuthenticated, isLoading } = useAdminAuth();
  const router = useRouter();
  const pathname = usePathname();

  useEffect(() => {
    // ローディング中は何もしない
    if (isLoading) return;

    // ログインページ以外で未認証の場合はリダイレクト
    if (!isAuthenticated && pathname !== "/admin/login") {
      router.push("/admin/login");
    }
  }, [isAuthenticated, isLoading, pathname, router]);

  // ローディング中は適切なUIを表示
  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-center">
          <div className="text-lg">Loading...</div>
        </div>
      </div>
    );
  }

  // 未認証時（ログインページ以外）は何も表示しない
  if (!isAuthenticated && pathname !== "/admin/login") {
    return null;
  }

  return children;
}
