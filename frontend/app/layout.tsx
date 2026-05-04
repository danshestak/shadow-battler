import Header from "./components/header/Header";
import "./globals.css";

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body className="min-h-full flex flex-col bg-theme2 text-text">
        <Header />
        <div className="flex-1 p-4">
          <main className="max-w-5xl m-auto">
            {children}
          </main>
        </div>
      </body>
    </html>
  );
}
