// SortableTierとUnassignedAreaの両方で使用する共通のドロッププレビューコンポーネント
const DropPreview = () => (
  <div className="relative w-[120px] h-[120px] m-0 transition-all duration-200">
    {/* 内側の光るボーダー */}
    <div
      className="absolute inset-0 rounded-lg border-2 border-indigo-400"
      style={{
        animation: "pulseIn 1.5s ease-in-out infinite",
      }}
    />
    {/* グラデーションの背景 */}
    <div
      className="absolute inset-0 rounded-lg opacity-20 bg-gradient-to-br from-indigo-400 to-purple-500"
      style={{
        animation: "fadeInOut 1.5s ease-in-out infinite",
      }}
    />
    {/* 外側のグロー効果 */}
    <div
      className="absolute inset-0 rounded-lg"
      style={{
        boxShadow: "0 0 15px rgba(99, 102, 241, 0.3)",
        animation: "glowPulse 1.5s ease-in-out infinite",
      }}
    />
    <style jsx>{`
      @keyframes pulseIn {
        0% {
          transform: scale(1);
          opacity: 0.8;
        }
        50% {
          transform: scale(1.02);
          opacity: 1;
        }
        100% {
          transform: scale(1);
          opacity: 0.8;
        }
      }

      @keyframes fadeInOut {
        0% {
          opacity: 0.1;
        }
        50% {
          opacity: 0.2;
        }
        100% {
          opacity: 0.1;
        }
      }

      @keyframes glowPulse {
        0% {
          box-shadow: 0 0 15px rgba(99, 102, 241, 0.2);
        }
        50% {
          box-shadow: 0 0 20px rgba(99, 102, 241, 0.4);
        }
        100% {
          box-shadow: 0 0 15px rgba(99, 102, 241, 0.2);
        }
      }
    `}</style>
  </div>
);

export default DropPreview;
