"use client";

import { Game } from '@/types/Game';
import axios from 'axios';
import Link from 'next/link';
import { useEffect, useState } from 'react';

const HomePage = () => {
    const [games, setGames] = useState<Game[]>([]);

    useEffect(() => {
        axios.get<Game[]>('http://localhost:8080/api/games')
            .then((res) => setGames(res.data as Game[]))
            .catch((err) => console.error(err));
    }, []);

    return (
        <div>
            <h1>ゲームを選択してください</h1>
            <ul>
                {games.map((game: Game) => (
                    <li key={game.id}>
                        <Link href={`/rankings?gameId=${game.id}`}>{game.name}</Link>
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default HomePage;
