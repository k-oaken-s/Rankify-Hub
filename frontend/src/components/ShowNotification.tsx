const ShowNotification = (message: string, type: "success" | "error" = "success") => {
  const notification = document.createElement("div");
  notification.className = `
    fixed top-4 left-1/2 px-6 py-3
    -translate-x-1/2 transform
    ${type === "success" ? "bg-green-600" : "bg-red-600"} text-white
    rounded-lg shadow-lg
    transition-all duration-500 ease-in-out
    z-50
  `;
  notification.textContent = message;
  document.body.appendChild(notification);

  notification.style.opacity = "0";
  notification.style.transform = "translate(-50%, -20px)";

  setTimeout(() => {
    notification.style.opacity = "1";
    notification.style.transform = "translate(-50%, 0)";
  }, 10);

  setTimeout(() => {
    notification.style.opacity = "0";
    notification.style.transform = "translate(-50%, -20px)";
    setTimeout(() => {
      document.body.removeChild(notification);
    }, 500);
  }, 3000);
};

export default ShowNotification;
