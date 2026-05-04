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
        <main className="p-2">
          {children}
        </main>
      </body>
    </html>
  );
}
