import { AdminAuthProvider } from "@/contexts/AdminAuthContext";
import { Analytics } from "@vercel/analytics/next";
// import "@ant-design/v5-patch-for-react-19";
import "tailwindcss/tailwind.css";

import Footer from "@/components/Footer";
import Header from "@/components/Header";

import "./globals.css";

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en" className="h-full">
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
