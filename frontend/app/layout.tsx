import Header from "@/components/header/Header";
import "./globals.css";
import { Outfit } from "next/font/google";
import { cn } from "@/lib/utils";

const font = Outfit({subsets:['latin'],variable:'--font-sans'});

const description = `Shadow Battler is the premier Team GO Rocket counters site for Pokémon GO. View grunts and leaders, which Pokémon defeat them, and run custom battle simulations`;
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
  },

  twitter: {
    card: 'summary',
    title: title,
    description: description
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
        <Header />
        <div className="flex-1 p-4">
          <main className="max-w-5xl m-auto">
            {children}
          </main>
        </div>
        <footer className="p-4 mt-4 text-center text-xs bg-theme3 border-t border-theme4 shadow-xl">
            Shadow Battler is not in any way affiliated with The Pokémon Company, Nintendo, Scopely, or Niantic.
            <br/>
            Pokémon is a trademark of Nintendo. All such trademarks are the property of their respective owners.
        </footer>
      </body>
    </html>
  );
}
