import { Dot } from "lucide-react"
import Link from "next/link"


const Footer = () => {
  return (
    <footer className="p-4 mt-4 text-center text-xs bg-theme3 border-t border-theme4 shadow-xl">
      <div className="flex gap-1 mb-2 justify-center items-center">
        <Link className='hover:underline' href='https://github.com/danshestak/shadow-battler'>GitHub</Link>
        <Dot className="size-4"/>
        <Link className='hover:underline' href='https://discord.gg/RdNV2BjZUV'>Discord</Link>
        <Dot className="size-4"/>
        <Link className='hover:underline' href='https://www.reddit.com/user/shadowbattler'>Reddit</Link>
      </div>
      Shadow Battler is not in any way affiliated with The Pokémon Company, Nintendo, Scopely, or Niantic.
      <br />
      Pokémon is a trademark of Nintendo. All such trademarks are the property of their respective owners.
    </footer>
  )
}

export default Footer