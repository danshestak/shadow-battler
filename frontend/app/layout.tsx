import Header from "@/components/layout/Header";
import "./globals.css";
import { Outfit } from "next/font/google";
import { cn } from "@/lib/utils";
import Footer from "@/components/layout/Footer";

const font = Outfit({subsets:['latin'],variable:'--font-sans'});

const description = 'Shadow Battler is the premier Team GO Rocket battle simulator for Pokémon GO. View opponents, compare the best counters, and run battle simulations.';
const title = 'Shadow Battler';
export const metadata = {
  title: {
    template: `%s - ${title}`,
    default: title
  },
  description: description,

  openGraph: {
    title: title,
    description: description,
    url: 'https://shadowbattler.com',
    siteName: title,
    locale: 'en_US',
    type: 'website',
    images: ['https://shadowbattler.com/opengraph.png'],
  },

  twitter: {
    card: 'summary_large_image',
    title: title,
    description: description,
    images: ['https://shadowbattler.com/opengraph.png'],
  },
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en" className={cn("font-sans", font.variable)}>
      <meta name="apple-mobile-web-app-title" content="Shadow Battler" />
      <body className="min-h-screen flex flex-col bg-theme2 text-text">
        <Header/>
        <div className="flex-1 p-4">
          <main className="max-w-3xl m-auto">
            {children}
          </main>
        </div>
        <Footer/>
      </body>
    </html>
  );
}
