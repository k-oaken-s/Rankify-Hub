// components/Footer.tsx
import Link from "next/link";

export default function Footer() {
  return (
    <footer className="bg-gray-800 text-gray-400 py-4 mt-auto border-t border-gray-700">
      <div className="container mx-auto px-4">
        <div className="flex flex-col items-center justify-between text-sm">
          <div className="flex flex-col sm:flex-row items-center text-center sm:text-left mb-2">
            <span className="mb-2 sm:mb-0">© {new Date().getFullYear()} Rankify Hub</span>
            <span className="hidden sm:inline mx-2">|</span>
            <span className="text-xs px-4 sm:px-0">
              本サイトで使用している画像は、各ゲームメーカーの利用ガイドラインに従って掲載しています。
              各画像の著作権は、それぞれの権利者に帰属します。
            </span>
          </div>

          {/*<div className="flex gap-4 text-xs">
        <Link href="/guidelines" className="hover:text-white transition-colors">
          ガイドライン
        </Link>
        <Link href="/terms" className="hover:text-white transition-colors">
          利用規約
        </Link>
        <Link href="/contact" className="hover:text-white transition-colors">
          お問い合わせ
        </Link>
      </div>*/}
        </div>
      </div>
    </footer>
  );
}
