"use client";

import {
  AppstoreOutlined,
  HomeOutlined,
  LikeOutlined,
  UnorderedListOutlined,
} from "@ant-design/icons";

import { useEffect, useState } from "react";

import Link from "next/link";
import { usePathname } from "next/navigation";

export default function Header() {
  const pathname = usePathname();
  const [selectedKey, setSelectedKey] = useState("1");

  useEffect(() => {
    if (pathname === "/") {
      setSelectedKey("1");
    } else if (pathname === "/categories") {
      setSelectedKey("2");
    } else if (pathname === "/tiers") {
      setSelectedKey("3");
    } else {
      setSelectedKey("");
    }
  }, [pathname]);

  const handleMenuClick = (key: string) => {
    setSelectedKey(key);
  };

  return (
    <header className="bg-gradient-to-r from-[#4b278f] to-[#28508f] shadow-lg">
      <div className="container mx-auto flex justify-between items-center p-4">
        <Link href="/">
          <h1
            className="text-2xl font-bold cursor-pointer hover:text-yellow-300 transition duration-200"
            onClick={() => setSelectedKey("1")}
          >
            Rankify Hub
          </h1>
        </Link>
        <nav className="flex space-x-4">
          <Link
            href="/"
            onClick={() => handleMenuClick("1")}
            className={`flex items-center space-x-2 px-4 py-2 rounded-md ${
              selectedKey === "1" ? "bg-yellow-300 text-black" : "hover:text-yellow-300"
            } transition duration-200`}
          >
            <HomeOutlined />
            <span>ホーム</span>
          </Link>
          <Link
            href="/categories"
            onClick={() => handleMenuClick("2")}
            className={`flex items-center space-x-2 px-4 py-2 rounded-md ${
              selectedKey === "2" ? "bg-yellow-300 text-black" : "hover:text-yellow-300"
            } transition duration-200`}
          >
            <AppstoreOutlined />
            <span>カテゴリーから新規作成</span>
          </Link>
          <Link
            href="/tiers"
            onClick={() => handleMenuClick("3")}
            className={`flex items-center space-x-2 px-4 py-2 rounded-md ${
              selectedKey === "3" ? "bg-yellow-300 text-black" : "hover:text-yellow-300"
            } transition duration-200`}
          >
            <UnorderedListOutlined />
            <span>公開されたTierを見る</span>
          </Link>
        </nav>
      </div>
    </header>
  );
}
