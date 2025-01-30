import { AdminAuthProvider } from "@/contexts/AdminAuthContext";
import { Analytics } from "@vercel/analytics/next";
import "tailwindcss/tailwind.css";

import { Noto_Sans_JP } from "next/font/google";

import Footer from "@/components/Footer";
import Header from "@/components/Header";

import "./globals.css";

const notoSansJp = Noto_Sans_JP({
  subsets: ["latin"],
  weight: ["400", "700"],
  preload: false,
  variable: "--font-noto-sans-jp",
  display: "swap",
});

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="ja" className={`h-full ${notoSansJp.variable}`}>
      <body className="bg-[#1f1f1f] text-white min-h-screen flex flex-col">
        <AdminAuthProvider>
          <Header />
          <main className="flex-grow container mx-auto p-6 bg-[#1f1f1f] text-gray-300">
            {children}
          </main>
          <Footer />
        </AdminAuthProvider>
        <Analytics />
      </body>
    </html>
  );
}
